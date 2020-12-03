package CSE3063_Project;

import java.util.Date;

    public class Logger {

        private Date date;
        private String type;
        private String operation;

        public Logger(Date date,String type, String operation) {
            this.date = date;
            this.type = type;
            this.operation = operation;
        }

        public String toString() {
            //return "Logger{" +
            //        "date=" + date +
              //      ", type='" + type + '\'' +
                //    ", operation='" + operation + '\'' +
                 //   '}';

            return date+ " INFO " + type + User.Id;
            // 12/12/2020 16:42:15.990 [UserManager] INFO userManager: created RandomLabelingMechanism1
            // as RandomBot
            //
            //12/12/2020 16:42:15.990 [InstanceTagger] INFO user id:2 RandomLabelingMechanism2
            // tagged instance id:4 with class label 2:Negative  instance:"siteniz çalışmıyor kaç gündür"
            //
            //12/12/2020 16:42:15.990 [InstanceTagger] INFO user id:1 RandomLabelingMechanism1
            // tagged instance id:4 with class label 3:Notr  instance:"siteniz çalışmıyor kaç gündür"














        }

    }
