from PythonProject.iteration1.main.models.Poll import Poll


class AttadancePoll(Poll):
    def setResult(self, result):
        self.result = result


    def setStudent(self, student):
        self.student = student


    def getStudent(self):
        return self.student


    def getResult(self):
        return self.result
