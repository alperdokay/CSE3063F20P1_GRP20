class AnswerKeyBuilder:
    def diff(self,li1, li2):
        return (list(list(set(li1) - set(li2)) + list(set(li2) - set(li1))))
    def build(self,answerKeys,polls):
        #TODO:Parse
        dataStore = {}
        targetKey = None
        correctAnswers = []
        for answerKey in answerKeys:
            for line in answerKey:
                #Check for if line contains Poll
                if("Poll" in line):
                    correctAnswers = []
                    print(line)
                    tempTargetKey = line.split(":")[1]
                    targetKey = tempTargetKey.split("  ")[0]
                    continue
                if("Answer" not in line):
                    #This means that line is Question
                    questionRaw = line
                    correctAnswers = []
                    question = questionRaw.split(".")[1]
                    questionToAdd = question.split("(")[0]
                    if(line == "3. .... variable’s value is shared by all instances of a class ( Single Choice)"):
                        questionToAdd = ".... variable’s value is shared by all instances of a class"
                    if(dataStore.get(targetKey) == None):
                        dataStore[targetKey] = {}
                    if(dataStore[targetKey].get(questionToAdd) == None):
                        dataStore[targetKey][questionToAdd] = correctAnswers
                else:
                    correctAnswers.append(line.split(":")[1])

        print(dataStore)
        for subPoll in polls:
            if(subPoll.type != "Quiz Polls"):
                continue
            pollQuestions = subPoll.statistics.keys()
            for pollName,questionAnswerPair in dataStore.items():
                buAnswerKeyBuPollaMiAit = False
                score = 0
                for q in pollQuestions:
                    if q.replace(" ","").lower().split("...")[0]  in [x.replace(" ","").lower() for x in list(questionAnswerPair.keys())]:
                        buAnswerKeyBuPollaMiAit = True
                        break

                if(buAnswerKeyBuPollaMiAit):
                    subPoll.name = pollName
                    subPoll.answerKey = questionAnswerPair

        print(polls)

