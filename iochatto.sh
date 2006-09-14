CLASSPATH=.:/opt/JXTA/bcprov-jdk14.jar:/opt/JXTA/javax.servlet.jar:/opt/JXTA/jdom.jar:/opt/JXTA/jxtacontrib.jar:/opt/JXTA/jxtaext.jar:/opt/JXTA/jxta.jar:/opt/JXTA/log4j.jar:/opt/JXTA/org.mortbay.jetty.jar:/opt/JXTA/swixml.jar:/opt/JXTA/gnu.getopt.jar


java -Xloggc:file=performancegc.txt  -cp $CLASSPATH it.di.unipi.iochatto.core.mainApp -b 30000 -e 31000 -r

