# Makefile to compile 

BINDIR=./bin
SRCDIR=./src
DOCDIR=./doc

.SUFFIXES: .java .class

${BINDIR}/%.class: ${SRCDIR}/%.java
	javac $< -cp ${BINDIR} -d ${BINDIR} 

# first build rule
${BINDIR}/C.class:${BINDIR}/CloudData.class ${BINDIR}/SeqCloudData.class ${BINDIR}/Prevail.class ${BINDIR}/Classify.class ${BINDIR}/ParallelCloudData.class 

run:
	java ${BINDIR}/CloudData.class "largesample_input.txt" "../largesample_output2.txt" "../largesample_output3.txt"

clean:
	rm -f ${BINDIR}/*.class

docs:
	javadoc  -classpath ${BINDIR} -d ${DOCDIR} ${SRCDIR}/*.java

cleandocs:
	rm -rf ${DOCDIR}/*
