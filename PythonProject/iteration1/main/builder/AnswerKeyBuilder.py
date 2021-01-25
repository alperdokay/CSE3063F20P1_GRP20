class AnswerKeyBuilder:

    def build(self,answerKeys,polls):

        pass
        # questionAnswerPair = {}
        # name = ""
        # for i in df.values:
        #     #TODO: ILk soru icin bug var /r/n bugi soru ismini yazdirmiyor hepsini garip bir bug
        #     if(len(i) > 1):
        #         x =  i[0]
        #
        #         questionAnswerPair[x] = i[1]
        #     else:
        #         name = i[0]
        # questionAnswerPairList = [i for i in questionAnswerPair.keys()]
        # questionAnswerPairList.sort()
        # for poll in polls:
        #     poll.questions.sort()
        #     isMatch = False
        #     for index in range(len(poll.questions)):
        #         if(len(poll.questions) != len(questionAnswerPairList)):
        #             break
        #         if(questionAnswerPairList[index] != poll.questions[index]):
        #             isMatch = False
        #             continue
        #         isMatch = True
        #     if(isMatch and hasattr(poll,"answerKey")):
        #         poll.answerKey = questionAnswerPair
        #         poll.name = name
        #     elif(isMatch and hasattr(poll,"answerKey") == False):
        #         poll.name = name
        #         poll.answerKey = questionAnswerPair
        #     attandanceQuestions = poll.attandanceQuestions
        #     quizQuestions = poll.quizQuesitons
        #     mergedQuestions = []
        #     mergedQuestions.extend(attandanceQuestions)
        #     mergedQuestions.extend(quizQuestions)
        #     for question in mergedQuestions:
        #         if(questionAnswerPair.get(question.question) is not None and question.answer == questionAnswerPair.get(question.question) ):
        #             question.result = True
        #         elif(questionAnswerPair.get(question.question) is not None and question.answer != questionAnswerPair.get(question.question) ):
        #             question.result = False


