from abc import ABC, abstractmethod


class AbstactStudentFactory(ABC):


    # @abstractmethod
    # def findByUniqueId(self,id):
    #     pass
    @abstractmethod
    def createRepoByUniqueID(self,setofElements,nameOfUniqueElement):
        pass


