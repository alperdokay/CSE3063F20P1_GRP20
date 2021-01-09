import math

import numpy
from numpy import *

from pandas import DataFrame

from PythonProject.iteration1.main.models.Question import Question


class PollBuilder:
    def __init__(self, dataFrame: DataFrame, studentRepository):
        """
        :type dataFrame: object
        """
        self.dataFrame = dataFrame
        self.studentRepo = studentRepository

    def build(self):
        questionLength = {}
        studentQuestionAnswerPair = {}
        existedQuestions = []
        for question in self.dataFrame.values:
            cleanedList = [x for x in question if str(x) != 'nan']
            if len(cleanedList) not in questionLength.keys():
                x = [cleanedList]
                questionLength[len(cleanedList)] = x
            else:
                questionLength[len(cleanedList)].append(cleanedList)
            studentEmail = cleanedList[2]
            tempStudentNameSurname = cleanedList[1]
            studentNameSurname = tempStudentNameSurname.replace(" ","").lower()
            if (studentNameSurname not in studentQuestionAnswerPair.keys()):
                studentQuestionAnswerPair[studentNameSurname] = [cleanedList[-int(len(cleanedList) - 4):]]
            else:
                studentQuestionAnswerPair[studentNameSurname].append(cleanedList[-int(len(cleanedList) - 4):])
            tempQuestions = cleanedList[-int(len(cleanedList) - 4):]
            for index in range(0, len(tempQuestions), 2):
                question, answer = tempQuestions[index], tempQuestions[index + 1]
                q = Question(question, answer)
                existedQuestions.append(q)
        print(questionLength)
        print(studentQuestionAnswerPair)
        print(existedQuestions)
