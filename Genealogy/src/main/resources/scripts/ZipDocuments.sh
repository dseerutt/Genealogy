zipfile(){
  echo "Start zipping documents - $(date +%H:%M:%S)"
  export PATH=$PATH:"C:\Program Files\7-Zip"
  FOLDER_TO_ZIP="D:\Genealogie\Data\*"
  DATE=$(date '+%Y%m%d')
  ZIP_NAME="\genealogyData_$DATE.7z"
  ZIP_FILE="D:\Genealogie\Data$ZIP_NAME"
  rm -f -- $ZIP_FILE
  PWD=$(cat "D:\Genealogie\Data\zip_pwd")
  7z a $ZIP_FILE $FOLDER_TO_ZIP -p$PWD -bsp1
}

main(){
  zipfile
}

main