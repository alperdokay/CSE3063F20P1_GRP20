import math
from collections import OrderedDict

import numpy
from numpy import *
from difflib import SequenceMatcher
import jellyfish
from pandas import DataFrame

from PythonProject.iteration1.main.factory.Factory import Factory
from PythonProject.iteration1.main.models.Question import Question


class PollBuilder:
    def __init__(self, pollName, dataFrame: DataFrame, studentRepository):
        """
        :type dataFrame: object
        """
        self.dataFrame = dataFrame
        self.studentRepo = studentRepository
        self.pollName = pollName

    def build(self):
        # TODO: First we need to understand if the poll consists more than 2 Types of polls like QuizPoll or Attandance Poll
        # Then we need group polls that are in same poll file. We need a mediator that will handle interaction between polls and
        # Students.
        Tr2Eng = str.maketrans("çğıöşü", "cgiosu")

        questionLength = {}
        studentQuestionAnswerPair = {}
        existedQuestions = []
        self.dataCleaning(questionLength,studentQuestionAnswerPair,existedQuestions)
        # TODO: Think about how to reduce complexity
        temp = OrderedDict()
        for student, questions in studentQuestionAnswerPair.items():
            temp[student] = {}
            for questionSet in questions:
                temp[student][questionSet[0].question] = questionSet[0].answer
        scores = OrderedDict()
        __keys = temp.keys()
        for pollStudents in __keys:
            tempArray = []
            for studentNumber, studentObject in self.studentRepo.numberPairStudentRepo.items():
                if (studentNumber in pollStudents):
                    score = 1.0
                    tempArray.append((studentObject, score))
                    continue
                # score = jellyfish.levenshtein_distance(pollStudents,studentObject.smartFullName)
                scoreSmart = self.similar(pollStudents.translate(Tr2Eng), studentObject.smartFullName.translate(Tr2Eng))
                scoreReal = self.similar(pollStudents.translate(Tr2Eng), studentObject.fullName.translate(Tr2Eng))
                if (scoreSmart >= scoreReal):
                    tempArray.append((studentObject, scoreSmart))
                else:
                    tempArray.append((studentObject, scoreReal))
            tempArray = sorted(tempArray, key=lambda x: x[1], reverse=True)[0]
            if (tempArray[1] < 0.80):
                continue
            scores[pollStudents] = tempArray

        orderedScores = OrderedDict()
        for i in sorted(scores.keys(), key=lambda x: x):
            orderedScores[i] = scores[i]
        missingStudents = []
        for missingStudent in studentQuestionAnswerPair.keys():
            if (missingStudent not in orderedScores.keys()):
                missingStudents.append((missingStudent))
        studentObjStudentPairs = {}
        for student, studentScorePair in orderedScores.items():
            questionsAnswered = studentQuestionAnswerPair[student]
            if (studentScorePair[0] in studentObjStudentPairs.keys()):
                print("HATAAAAA")
            else:
                studentObjStudentPairs[studentScorePair[0]] = questionsAnswered
        print(studentObjStudentPairs)

    def similar(self, a, b):
        return SequenceMatcher(None, a, b).ratio()

    def dataCleaning(self, questionLength=None, studentQuestionAnswerPair=None, existedQuestions=None):
        for question in self.dataFrame.values:
            cleanedList = [x for x in question if str(x) != 'nan']
            if len(cleanedList) not in questionLength.keys():
                x = [cleanedList]
                questionLength[len(cleanedList)] = x
            else:
                questionLength[len(cleanedList)].append(cleanedList)
            studentEmail = cleanedList[2]
            tempStudentNameSurname = cleanedList[1]
            studentNameSurname = tempStudentNameSurname.replace(" ", "").lower()
            tempQuestions = cleanedList[-int(len(cleanedList) - 4):]
            if (studentNameSurname not in studentQuestionAnswerPair.keys()):
                temp = []
                for index in range(0, len(tempQuestions), 2):
                    question, answer = tempQuestions[index], tempQuestions[index + 1]
                    tempFactory = Factory()
                    question = tempFactory.create(Question, (question, answer))
                    temp.append(question)
                studentQuestionAnswerPair[studentNameSurname] = [temp]
            else:
                temp = []
                for index in range(0, len(tempQuestions), 2):
                    question, answer = tempQuestions[index], tempQuestions[index + 1]
                    tempFactory = Factory()
                    question = tempFactory.create(Question, (question, answer))
                    temp.append(question)

                studentQuestionAnswerPair[studentNameSurname].append(temp)
            for index in range(0, len(tempQuestions), 2):
                question, answer = tempQuestions[index], tempQuestions[index + 1]
                if (question not in existedQuestions):
                    existedQuestions.append(question)
