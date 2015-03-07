_DIR_THIS="$( cd $(dirname $0); pwd -P)"
_DIR_RAM="/dev/shm/ShowCaseStandalone-target"

_DIR_TARGET="$_DIR_THIS/target"


echo "dir_this: $_DIR_THIS"
echo "dir_ram:  $_DIR_RAM"
echo "dir_targ: $_DIR_TARGET"
echo ""

# just check that it exists... (it might get lost on reboot, while the link still exists)
mkdir -p $_DIR_RAM

# delete the real directory (so the link can be created)
if [[ -d $_DIR_TARGET ]]; then
	rm -R $_DIR_TARGET
fi

# be sure the link does not exist, if sure, create
if [[ ! -f $_DIR_TARGET ]]; then 
	ln -s $_DIR_RAM $_DIR_TARGET
fi


#(cd $_DIR_THIS; mvn install -Dmaven.javadoc.skip=true && (cd $_DIR_TARGET; java -jar jWebsite-0.0.1-SNAPSHOT.jar));
#(cd $_DIR_THIS; mvn compile -Dmaven.javadoc.skip=true && (cd $_DIR_TARGET/classes; java -cp . com.kellerkindt.website.core.Main));
(cd $_DIR_THIS; mvn install -Dmaven.javadoc.skip=true);
