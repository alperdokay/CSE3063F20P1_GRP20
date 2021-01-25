import math
from collections import OrderedDict

import numpy
from numpy import *
from difflib import SequenceMatcher
import jellyfish
from pandas import DataFrame
import datetime
from PythonProject.iteration1.main.factory.Factory import Factory
from PythonProject.iteration1.main.models.Poll import Poll
from PythonProject.iteration1.main.models.Question import Question

import json


class PollBuilder:
    def __init__(self, pollName, dataFrame: DataFrame, studentRepository):
        """
        :type dataFrame: object
        """
        self.dataFrame = dataFrame
        self.studentRepo = studentRepository
        self.pollPath = pollName
        self.cleanPollData = self.cleanData()
        self.Tr2Eng = str.maketrans("çğıöşü", "cgiosu")
    def build(self,quizListener):
        print("Started to parse",self.pollPath)
        reportGenerated = self.cleanPollData[1][1]
        sessionName = self.cleanPollData[3][0]
        date = datetime.datetime.strptime(self.cleanPollData[3][2],'%Y-%m-%d %H:%M:%S')
        self.missingStudents = []
        migrationFromStudentList = {}
        self.studentsInPoll = self.studentsInPolls()

        self.orderedScores = self.findNames()
        self.missingStudents = [[name, self.studentsInPoll[name]] for name in self.studentsInPoll if
                                name not in self.orderedScores.keys()]

        self.pollStudentPair = self.pollsInPolls()
        factory = Factory()
        poll = Poll(date,sessionName,self.pollPath,self.missingStudents,self.pollStudentPair,reportGenerated,self.orderedScores)
        print("finished parsing",self.pollPath)
        return poll
    def pollsInPolls(self):
        attendanceQuizes = []
        temp = OrderedDict()
        existedQuestions = []
        groupedExistedQuestions = {}
        factory = Factory()
        for question in self.cleanPollData[6:]:
            questions = []
            for questionIndex in range(0, len(question[4:len(question) - 1]), 2):
                tempQuestion = question[4:len(question) - 1][questionIndex]
                questions.append(tempQuestion)
            buSoruDahaOncekiSorulardaVarmi = False
            for thisPollQuestion in questions:
                if(thisPollQuestion in existedQuestions):
                    buSoruDahaOncekiSorulardaVarmi = True
                    break
            if(question[4] not in temp and buSoruDahaOncekiSorulardaVarmi == False):
                temp[question[4]] = {}
                for q in question[4:len(question) - 1]:
                    groupedExistedQuestions[q] = question[4:len(question) - 1]


            if(question[1].replace(" ","").lower().translate(self.Tr2Eng) not in self.orderedScores.keys()):
                continue
            try:
                temp[question[4]][self.orderedScores[question[1].replace(" ","").lower().translate(self.Tr2Eng)]] = [factory.create(Question,(question[4:len(question)-1][questionIndex],question[4:len(question)-1][questionIndex+1]))for questionIndex in range(0,len(question[4:len(question)-1]),2)]
            except KeyError as e:
                temp[groupedExistedQuestions[question[4]][0]][self.orderedScores[question[1].replace(" ","").lower().translate(self.Tr2Eng)]] = [factory.create(Question,(question[4:len(question)-1][questionIndex],question[4:len(question)-1][questionIndex+1]))for questionIndex in range(0,len(question[4:len(question)-1]),2)]

            existedQuestions.extend(questions)
        typeGroupedQuestions = {}
        typeGroupedQuestions["Attendance Polls"] = []
        typeGroupedQuestions["Quiz Polls"] = []

        for key in temp.keys():
            if(key == "Are you attending this lecture?"):
                typeGroupedQuestions["Attendance Polls"].append(temp[key])
            else:
                typeGroupedQuestions["Quiz Polls"].append(temp[key])
        return typeGroupedQuestions
    def findNames(self):
        scores = OrderedDict()
        for pollStudents in self.studentsInPoll.keys():
            tempArray = []
            for studentNumber, studentObject in self.studentRepo.numberPairStudentRepo.items():
                if (studentNumber in pollStudents):
                    score = 1.0
                    tempArray.append((studentObject, score))
                    continue
                scoreSmart = self.similarityRatio(pollStudents.translate(self.Tr2Eng),
                                                  studentObject.smartFullName.translate(self.Tr2Eng))
                scoreReal = self.similarityRatio(pollStudents.translate(self.Tr2Eng),
                                                 studentObject.fullName.translate(self.Tr2Eng))
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
            orderedScores[i] = scores[i][0]
        return orderedScores
    def studentsInPolls(self):
        studentMailPair = {}

        for question in self.cleanPollData[6:]:
            name,mail,dateQuestionAnswered = question[1],question[2],question[3]
            targetName = name.replace(" ","").lower().translate(self.Tr2Eng)
            studentMailPair[targetName] = mail
        return studentMailPair
    def similarityRatio(self, fullName, studentMail):
        return SequenceMatcher(None, fullName, studentMail).ratio()


    def exportMissingStudentsToJson(self, missingStudents):
        with open('../assets/anomalies.json', 'w') as file:
            json.dump(missingStudents, file)

    def oldBuild(self,quizListener):
        Tr2Eng = str.maketrans("çğıöşü", "cgiosu")
        pollObj = Factory().createWithoutParameters(Poll)
        questionLength = {}
        studentQuestionAnswerPair = {}
        existedQuestions = []
        pollObj.setQuestions(existedQuestions)
        self.dataCleaning(questionLength, studentQuestionAnswerPair, existedQuestions)
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
                scoreSmart = self.similarityRatio(pollStudents.translate(Tr2Eng),
                                                  studentObject.smartFullName.translate(Tr2Eng))
                scoreReal = self.similarityRatio(pollStudents.translate(Tr2Eng),
                                                 studentObject.fullName.translate(Tr2Eng))
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
        self.exportMissingStudentsToJson(missingStudent)  # exporting anomalies to json
        studentObjStudentPairs = {}
        for student, studentScorePair in orderedScores.items():
            questionsAnswered = studentQuestionAnswerPair[student]
            if (studentScorePair[0] in studentObjStudentPairs.keys()):
                print("Hatalı Durum")
            else:
                studentObjStudentPairs[studentScorePair[0]] = questionsAnswered
        tempAttandanceQuestions = []
        tempQuizQuestions = []
        for student, listOfQuestonsStudentAnswered in studentObjStudentPairs.items():
            for questionCollection in listOfQuestonsStudentAnswered:
                for q in questionCollection:
                    q.student = student
                    if (q.question == "Are you attending this lecture?"):
                        tempAttandanceQuestions.append(q)
                        break
                    else:
                        tempQuizQuestions.append(q)

        pollObj.setAttadanceQuestions(tempAttandanceQuestions, pollObj)
        pollObj.setQuizQuestions(tempQuizQuestions, pollObj)
        pollObj.setQuestions(existedQuestions)
        quizListener.fire(type="opearation", value="add")
        return pollObj

    def cleanData(self):
        cleanData = []
        for row in self.dataFrame.values:
            cleanData.append(self.getWithoutNone(list(row)))
        return cleanData

    def getWithoutNone(self,data):
        clean = []
        for temp in data:
            if(temp is None):
                continue
            else:
                clean.append(temp)
        return clean

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