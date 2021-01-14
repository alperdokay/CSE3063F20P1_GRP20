class Poll:

    # def __init__(self,date,type,studentList,questions,answerByStudents ):
    #     # self.date = date
    #     # self.studentList = studentList
    #     # self.questions = questions
    #     # self.answerByStudents = answerByStudents


    def setDate(self, date):
        self.date = date


    def setType(self, type):
        self.type = type


    def setStudentList(self, studentList):
        #to make student names camelcase
        self.studentList = studentList
        self.refactored_names = []
        for studentName in self.studentList:
            string = studentName.split()
            camelCasedName = string[0].lower() + ''.join(ele.title() for ele in string[1:])
            self.refactored_names.append(camelCasedName)

    def setQuestions(self, questions):
        self.questions = questions


    def setanswerByStudents(self, answerByStudents):
        self.answerByStudents = answerByStudents

    def getStudentList(self):
        return self.refactored_names


    def getDate(self):
        return self.date


    def getType(self):
        return self.type


    def getQuestions(self):
        return self.questions

    def getanswerByStudents(self):
        return self.answerByStudents

    def getReport(self,type):
        pass

    def setQuizQuestions(self,quizQuestionsCollection,object):
        self.quizQuesitons = quizQuestionsCollection
        self.quizQuesitonsPair = {}
        for question in self.quizQuesitons:
            if(question.student in self.quizQuesitonsPair.keys()):
                self.quizQuesitonsPair.get(question.student).append(question)
            else:
                self.quizQuesitonsPair[question.student] = [question]
        for student,questions in self.quizQuesitonsPair.items():
            student.setPollQuestionPair(poll=self,questions=questions,type="Quiz")
            print("done")

    def setAttadanceQuestions(self,attedanceQuesitonsCollection,object):
        self.attandanceQuestions = attedanceQuesitonsCollection
        self.attandanceQuestionsPair = {}
        for question in self.attandanceQuestions:
            if (question.student in self.attandanceQuestionsPair.keys()):
                self.attandanceQuestionsPair.get(question.student).append(question)
            else:
                self.attandanceQuestionsPair[question.student] = [question]

        for student,questions in self.attandanceQuestionsPair.items():
            student.setPollQuestionPair(poll=self,questions=questions,type="Attandance")
            print("done")
