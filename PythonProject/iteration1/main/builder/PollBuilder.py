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
        #TODO: First we need to understand if the poll consists more than 2 Types of polls like QuizPoll or Attandance Poll
        # Then we need group polls that are in same poll file. We need a mediator that will handle interaction between polls and
        # Students.

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
                if(question not  in existedQuestions):
                    existedQuestions.append(question)
        #TODO: Think about how to reduce complexity
        temp = {}
        for student,questions in studentQuestionAnswerPair.items():
            temp[student] = {}
            for questionSet in questions:
                for q in range(0,len(questionSet),2):
                    print(questionSet[q],questionSet[q+1])
                    temp[student][questionSet[q]] = questionSet[q+1]
        print(temp)
