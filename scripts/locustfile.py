from locust import HttpLocust, TaskSet, task
import random, math, queue
import requests

students_number = 10000
headers = {
    "Content-Type": "application/json"
}
# print("starting db")
# requests.session().post("http://192.168.0.15:9000/seed-for-stress", headers=headers, params={'amount': students_number})
# print("")

students = [i for i in range(1, students_number + 1)]

students_q = queue.Queue(students_number + 1)
for student in students:
  students_q.put(student)

def login(l):
  with l.client.post("/login", json={"username": l.username, "password": l.password}, headers=headers, verify=False, catch_response=True) as response:
    if response.status_code == 401:
      response.success()
      i = students_q.get()
      l.username = f"student{i}"
      l.password = f"password{i}"
      l.filenumber = f"{i}"
      login(l)


def logout(l):
  l.client.post("/logout", json={"username": l.username, "password": l.password},
                headers=headers, verify=False)

class UserBehavior(TaskSet):

  def __init__(self, other):
    TaskSet.__init__(self, other)
    self.username = None
    self.password = None
    self.filenumber = None
    self.careers = []
    self.poll_results = []
    self.selected_career = None
    self.selected_poll = None
    self.choice = dict()
    self.poll_response = None
    self.index = 0

  def get_next_task(self):
    task = self.tasks[self.index]
    self.index = (self.index + 1) % len(self.tasks)
    return task


  def on_start(self):
    i = students_q.get()
    self.username = f"student{i}"
    self.password = f"password{i}"
    self.filenumber = f"{i}"
    self.tasks.sort(key=lambda t: t.locust_task_weight)
    login(self)

  @task(1)
  def get_info(self):
    response = self.client.get(f"/students/{self.filenumber}", verify=False)
    info = response.json()
    self.careers = info['careers']
    self.pollResults = info['pollResults']
    self.polls = info["polls"]

  @task(1)
  def get_poll_result(self):
    self.selected_career = self.get_random_career()
    self.selected_poll = self.get_random_poll()
    response = self.client.get(f"/careers/{self.selected_career['shortName']}/polls/{self.selected_poll['key']}", verify=False)
    self.poll_response = response.json()
    with self.client.get(f"/students/{self.filenumber}/careers/{self.selected_career['shortName']}/poll-result/{self.selected_poll['key']}", verify=False, catch_response=True) as response2:
      if response2.status_code == 404:
        response2.success()
        self.client.post(f"/students/{self.filenumber}/careers/{self.selected_career['shortName']}/poll-result/{self.selected_poll['key']}", json={}, headers=headers, verify=False)

  @task(1)
  def do_send_choice(self):
    for _ in range(math.ceil(max(1, random.gauss(3, 1)))):
      self.send_choice()

  def send_choice(self):
    self.make_your_choice()
    response = self.client.patch(
        f"/students/{self.filenumber}/careers/{self.selected_career['shortName']}/poll-result/{self.selected_poll['key']}",
        json=self.choice, headers=headers, verify=False)

  def on_stop(self):
    logout(self)

  def make_your_choice(self):
    for key, value in self.poll_response['offer'].items():
      if random.getrandbits(1) is 1:
        self.choice[key] = self.to_delta(random.choice(value))

  def get_random_career(self):
    return random.choice(self.careers)


  def get_random_poll(self):
    return random.choice(self.polls)


  def to_delta(self, value):
    value.pop('schedules', None)
    value.pop('quota', None)
    return value

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 30000
