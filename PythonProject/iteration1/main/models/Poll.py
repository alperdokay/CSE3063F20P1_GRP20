class Poll:

    def __init__(self, ):
        pass

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
