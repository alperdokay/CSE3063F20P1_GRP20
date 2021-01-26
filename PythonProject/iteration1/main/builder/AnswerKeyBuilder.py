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
                line = str(line).strip()
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
                    question = questionRaw
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
        for p in polls:
            for q in p.statistics.keys():
                for answerkeyName , data in dataStore.items():
                    for answerKeyQuestion  in data.keys():
                        if(q.split("(")[0].strip() in answerKeyQuestion or answerKeyQuestion in q.split("(")[0].strip()):
                            p.name = answerkeyName
                            p.answerKey = data
                            p.evaluate()

        print(polls)

