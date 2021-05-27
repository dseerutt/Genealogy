zipFile(){
  echo "Start zipping documents - $(date +%H:%M:%S)"
  export PATH=$PATH:"C:\Program Files\7-Zip"
  FOLDER_TO_ZIP="D:\Genealogie\Data\*"
  DATE=$(date '+%Y%m%d')
  ZIP_NAME="\genealogyData_$DATE.7z"
  ZIP_FILE="D:\Genealogie\Data$ZIP_NAME"
  rm -f -- $ZIP_FILE
  7z a $ZIP_FILE $FOLDER_TO_ZIP -p$ZIP_PWD -bsp1 -v200m #-x!*\Preuves\
  
  RETURN_CODE=$?
    if [ "$RETURN_CODE" -eq 0 ]; then
      echo "Zip OK"
    else
      echo "Zip KO"
      exit $RETURN_CODE
    fi
}

getFileToRemove(){
  echo "Get Dropbox file to remove"
  HTTP_CODE=$(curl -X POST -sL https://api.dropboxapi.com/2/files/list_folder \
      --header "Authorization: Bearer $DROPBOX_TOKEN1" \
      --header "Content-Type: application/json" \
      --data "{\"path\": \"$DROPBOX_DIR/\"}")
  DROPBOX_FILE1=$(echo "$HTTP_CODE" | jq '.entries[].path_display?' | sed 's|"||g')
  echo "File(s) to remove on first repo : $DROPBOX_FILE1"
  
  HTTP_CODE=$(curl -X POST -sL https://api.dropboxapi.com/2/files/list_folder \
      --header "Authorization: Bearer $DROPBOX_TOKEN2" \
      --header "Content-Type: application/json" \
      --data "{\"path\": \"$DROPBOX_DIR/\"}")
  DROPBOX_FILE2=$(echo "$HTTP_CODE" | jq '.entries[].path_display?' | sed 's|"||g')
  echo "File(s) to remove on second repo : $DROPBOX_FILE2"
}

uploadFiles(){
  FILE_UPLOAD="$1"
  LS_BASE_NAME=$(echo "$FILE_UPLOAD" | sed 's/\\/\//g')
  LS_NAME=$(ls $LS_BASE_NAME*)
  FILE_NAME_UPLOAD=$(echo "$2" | sed 's/\\/\//g')
	arrFILE=(${LS_NAME// / })
	TOKEN="$DROPBOX_TOKEN1"
	for index in ${!arrFILE[@]}; do
		if [ $index -gt 8 ]; then
			echo "Switch to second repo"
			TOKEN="$DROPBOX_TOKEN2"
		fi
        FILE_INDEX=${arrFILE[$index]}
		INDEX_FILE=$(printf "%03d\n" $((index+1)))
        uploadFile "$FILE_INDEX" "$FILE_NAME_UPLOAD.$INDEX_FILE" "$TOKEN"
	done
}

uploadFile(){
  FILE_UPLOAD="$1"
  FILE_NAME=$(echo "$2" | sed 's/\\/\//g')
  TOKEN="$3"
  echo "Upload $FILE_UPLOAD - $FILE_NAME"
    # upload file to dropbox
    HTTP_CODE=$(curl -X POST https://content.dropboxapi.com/2/files/upload -w "%{http_code}" -o /dev/null \
      --header "Authorization: Bearer $TOKEN" \
      --header "Dropbox-API-Arg: {\"path\": \"/Genealogy$FILE_NAME\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false,\"strict_conflict\": false}" \
      --header "Content-Type: application/octet-stream" \
      --data-binary @$FILE_UPLOAD)
    if [ "$HTTP_CODE" == "200" ]; then
      echo "Upload OK"
    else
      echo "Upload KO"
      exit 100
    fi
}

removeFileNotEmpty(){
  FILE="$1"
  TOKEN="$2"
  TEXT="$3"
    if [[ ! -z "$FILE" && "$FILE" != "null" ]]; then
	  arrFILE=(${FILE// / })
	  for index in ${!arrFILE[@]}; do
        FILE_INDEX=${arrFILE[$index]}
		echo "Will remove file $FILE_INDEX"
        removeFile "$FILE_INDEX" "$TOKEN"
	  done
    else
      echo "WARNING - No file to remove on $TEXT repo"
    fi
}

removeFile(){
  FILE="$1"
  BASENAME=$(basename $FILE)
  TOKEN="$2"
  echo "Remove file $FILE"
    # delete file from dropbox
    HTTP_CODE=$(curl -X POST -sL -w "%{http_code}" https://api.dropboxapi.com/2/files/delete_v2 -w "%{http_code}" -o /dev/null\
        --header "Authorization: Bearer $TOKEN" \
        --header "Content-Type: application/json" \
        --data "{\"path\": \"$DROPBOX_DIR/$BASENAME\"}")
    if [ "$HTTP_CODE" == "200" ]; then
      echo "Deletion OK"
    else
      echo "Deletion KO"
      exit 110
    fi
}

removeLocalZipFiles(){
  FILE_UPLOAD="$1"
  LS_BASE_NAME=$(echo "$FILE_UPLOAD" | sed 's/\\/\//g')
  LS_NAME=$(ls $LS_BASE_NAME*)
  FILE_NAME_UPLOAD=$(echo "$2" | sed 's/\\/\//g')
	arrFILE=(${LS_NAME// / })
	for index in ${!arrFILE[@]}; do
        FILE_INDEX=${arrFILE[$index]}
		echo "Suppression de $FILE_INDEX"
		rm "$FILE_INDEX"
	done
}

main(){
  #alias save="C:/Users/Dan/Desktop/Programmation/IntelliJ/Genealogy/Genealogy/src/main/resources/scripts/ZipDocuments.sh"
  ZIP_PWD=$(cat "D:\Genealogie\zip_pwd")
  DROPBOX_TOKEN1=$(cat "D:\Genealogie\dropbox_pwd1")
  DROPBOX_TOKEN2=$(cat "D:\Genealogie\dropbox_pwd2")
  DROPBOX_DIR="/Genealogy"
  zipFile
  getFileToRemove
  removeFileNotEmpty "$DROPBOX_FILE1" "$DROPBOX_TOKEN1" "first"
  removeFileNotEmpty "$DROPBOX_FILE2" "$DROPBOX_TOKEN2" "second"
  uploadFiles "$ZIP_FILE" "$ZIP_NAME"
  removeLocalZipFiles "$ZIP_FILE"
  read -rsp $'End of treatment - '$(date +%H:%M:%S)
  exit 0
}

main