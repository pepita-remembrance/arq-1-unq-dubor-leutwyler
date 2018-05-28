import requests
import random
import json

class Student:
    def __init__(self, url="http://localhost:9000", username="marcogomez", password="123", filenumber="123"):
        self.username = username
        self.password = password
        self.url = url
        self.filenumber = filenumber
        self.s = requests.session()
        self.careers = []
        self.poll_results = []
        self.selected_career = None
        self.selected_poll = None
        self.choice = dict()
        self.poll_response = None
        self.elapsed = 0


    def login(self):
        data = {
            "username": self.username,
            "password": self.password
            }
        headers = {
            "Content-Type" : "application/json"
            }
        response = self.s.post(f"{self.url}/login", json=data, headers=headers)
        self.elapsed += response.elapsed.total_seconds()
        if response.status_code is not 200:
            raise Exception("There was an error in the login")


    def get_info(self):
        response = self.s.get(f"{self.url}/students/{self.filenumber}")
        self.elapsed += response.elapsed.total_seconds()
        info = response.json()
        self.careers = info['careers']
        self.pollResults = info['pollResults']
        self.polls = info["polls"]


    def get_poll_result(self):
        self.selected_career = self.get_random_career()
        self.selected_poll = self.get_random_poll()
        response = self.s.get(f"{self.url}/careers/{self.selected_career['shortName']}/polls/{self.selected_poll['key']}")
        self.elapsed += response.elapsed.total_seconds()
        self.poll_response = response.json()


    def make_your_choice(self):
        for key, value in self.poll_response['offer'].items():
            if random.getrandbits(1) is 1:
                self.choice[key] = self.to_delta(random.choice(value))

    def send_choice(self):
        headers = {
            "Content-Type" : "application/json"
            }
        response = self.s.patch(
            f"{self.url}/students/{self.filenumber}/careers/{self.selected_career['shortName']}/poll-result/{self.selected_poll['key']}",
            json=self.choice, headers=headers)
        self.elapsed += response.elapsed.total_seconds()


    def get_random_career(self):
        return random.choice(self.careers)


    def get_random_poll(self):
        return random.choice(self.polls)


    def to_delta(self, value):
        value.pop('schedules', None)
        value.pop('quota', None)
        return value



def average_time_colour(avg_elapsed):
    return '\033[91m' if avg_elapsed >= 1 else ('\033[93m' if avg_elapsed >= 0.5 else '\033[92m')



if __name__ == "__main__":
    student = Student()
    student.login()
    student.get_info()
    student.get_poll_result()
    student.make_your_choice()
    student.send_choice()
    print(
        average_time_colour(student.elapsed / 4),
        '\033[1m' + '\033[4m' +
        f"{student.username} requests took average {int(student.elapsed / 4 * 1000)}ms each")
