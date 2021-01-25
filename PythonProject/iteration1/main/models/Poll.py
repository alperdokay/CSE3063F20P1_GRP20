class Poll:

    def __init__(self,date,session,path,missingStudents,polls,generationTime,students):
        self.generationTime = generationTime
        self.date = date
        self.session = session
        self.path = path
        self.missingStudents = missingStudents
        self.polls = polls
        self.students = students
        self.answerKey = None
        self.name = None
        self.type = None
    # def setStudentList(self, studentList):
    #     self.studentList = studentList
    #     self.refactored_names = []
    #     for studentName in self.studentList:
    #         string = studentName.split()
    #         camelCasedName = string[0].lower() + ''.join(ele.title() for ele in string[1:])
    #         self.refactored_names.append(camelCasedName)
    #
    # def setQuestions(self, questions):
    #     self.questions = questions
    #
    # def setanswerByStudents(self, answerByStudents):
    #     self.answerByStudents = answerByStudents
    #
    # def getStudentList(self):
    #     return self.refactored_names
    #
    # def getType(self):
    #     return self.type
    #
    # def getQuestions(self):
    #     return self.questions
    #
    # def getanswerByStudents(self):
    #     return self.answerByStudents

    # def setQuizQuestions(self, quizQuestionsCollection, object):
    #     self.quizQuesitons = quizQuestionsCollection
    #     self.quizQuesitonsPair = {}
    #     for question in self.quizQuesitons:
    #         if (question.student in self.quizQuesitonsPair.keys()):
    #             self.quizQuesitonsPair.get(question.student).append(question)
    #         else:
    #             self.quizQuesitonsPair[question.student] = [question]
    #     for student, questions in self.quizQuesitonsPair.items():
    #         student.setPollQuestionPair(poll=self, questions=questions, type="Quiz")
    #
    # def setAttadanceQuestions(self, attedanceQuesitonsCollection, object):
    #     self.attandanceQuestions = attedanceQuesitonsCollection
    #     self.attandanceQuestionsPair = {}
    #     for question in self.attandanceQuestions:
    #         if (question.student in self.attandanceQuestionsPair.keys()):
    #             self.attandanceQuestionsPair.get(question.student).append(question)
    #         else:
    #             self.attandanceQuestionsPair[question.student] = [question]
    #
    #     for student, questions in self.attandanceQuestionsPair.items():
    #         student.setPollQuestionPair(poll=self, questions=questions, type="Attandance")

class SubPoll:
    def __init__(self,date,session,path,missingStudents,polls,generationTime,students):
        self.generationTime = generationTime
        self.date = date
        self.session = session
        self.path = path
        self.missingStudents = missingStudents
        self.polls = polls
        self.students = students
        self.answerKey = None
        self.name = None
        self.type = None
        self.generateQuestions()

    def generateQuestions(self):
        self.statistics = {}
        for student,listOfQuestions in self.polls.items():
            for question in listOfQuestions:
                if(self.statistics.get(question.question) == None):
                    self.statistics[question.question] = {}
                    self.statistics[question.question][question.answer] = 1
                else:
                    if(self.statistics[question.question].get(question.answer) == None):
                        self.statistics.get(question.question)[question.answer] = 1
                    else:
                        self.statistics.get(question.question)[question.answer] = self.statistics.get(question.question)[question.answer] + 1
