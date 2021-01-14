class AnswerKey:

    def __init__(self,question,result,answer ):
        self.questionsAnswers = {}
        self.poll = None

    def setQuestion(self, question):
        self.question = question

    def getQuestion(self):
        return self.question

