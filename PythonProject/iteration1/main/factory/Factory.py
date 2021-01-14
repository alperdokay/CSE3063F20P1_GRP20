class Factory():

    def create(self, typeOfClass, arguments):
        instance = typeOfClass(*arguments)
        return instance
    def createWithoutParameters(self, typeOfClass):
        instance = typeOfClass()
        return instance
