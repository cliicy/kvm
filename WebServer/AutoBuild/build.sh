if [ -n "$D2D_BASE" -a -d "$D2D_BASE" ]; then
	ABPATH="$D2D_BASE/WebServer/AutoBuild"
	CUR=`pwd`
	cd "$ABPATH"
else
	ABPATH=.
	CUR=""
fi

export PATH=$ABPATH/apache-ant-1.7.1/bin:$PATH
export ANT_HOME=$ABPATH\apache-ant-1.7.1
export ANT_OPTS=-Xmx512m
#ant -buildfile build.xml 
ant
RET=$? 
[ -n "$CUR" ] && cd "$CUR"
exit $RET
