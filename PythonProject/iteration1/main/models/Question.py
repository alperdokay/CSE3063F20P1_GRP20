class Question:

    def __init__(self, question, answer):
        self.question = question
        self.answer = answer
        self.result = None
        self.correctResult = None
    def setStudent(self, student):
        self.student = student
