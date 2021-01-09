from abc import ABC, abstractmethod


class AbstactRepository(ABC):


    # @abstractmethod
    # def findByUniqueId(self,id):
    #     pass
    @abstractmethod
    def createRepoByUniqueID(self,setofElements,nameOfUniqueElement):
        pass


