<?php

//Global Constants
$server_version = "keke_02/11/11-10:03";
$server_version_prev = "eoriou_12/10/11-17:11";
$SHOW_VERSION = true;

//Open DB connection
$link = mysql_connect('localhost', 'root', '');
mysql_select_db('androtex');

//------------------------
// COMMON FUNCTIONS
//------------------------

/**
* Returns true if the authentication was a success, false otherwise
* @return the authentication status
*/

function authenticate($login, $password){
  // update >> encodePassword will encode a previous encoded one !
  // mysql_real_escape_string and htmlentities
  $password = mysql_real_escape_string(htmlentities($password));
  $res = mysql_query("SELECT count(*) as res FROM users WHERE login='".$login."' AND password='".$password."'");
  $fetch = mysql_fetch_array($res);
  $resultat = intval($fetch["res"]);
  return $resultat == 1;
}

/**
* Returns the hash corresponding to the password submitted
* @return the hash
*/
function encodePassword($password){
  return sha1("SABLE".$password.md5($password));
}
/**
* Build a JSON response from an associative array
* @return the JSON response
*/

function buildJSONResponse($attr_array){
  global $SHOW_VERSION;
  global $server_version;
  $i=0;
  $result = "{";
  
  if($SHOW_VERSION == true){
    $attr_array["version"] = $server_version;
  }

  foreach ($attr_array as $key => $value) {
    if(is_array($value)){
      $tmp = "";
      for($j=0;$j<count($value);$j++){
        $tmp .= ( ($j>0) ? "," : "" )."'".$value[$j]."'";
      }
      $result .= "'".$key."':[".$tmp."]";
    }else{
      $result .= "'".$key."':'".$value."'";
    }
    if(++$i < sizeof($attr_array)){
          $result .= ",";
    }
  }
  return $result .= "}";
}

/**
* Return the error code in a JSON structure
* code 1 : Check error
* code 2 : Auth error
* code 3 : Hierarchy error
* code 4 : Malformed request
*/

function returnErrorCode($err_code){
  return buildJSONResponse(array("create" => "erreur", "code" => $err_code));
}

/**
* Launch latex compilation
*/

function compile($login, $project, $file){
  ob_start();
  if(isTex($file))
    system("cd ".$login."\\".$project." && pdflatex ".$file);
    echo "";
  $tampon = ob_get_contents();
  ob_end_clean();
  return base64_encode($tampon);
}

/**
* Remove unsafe Latex command into the code
*/

function latexRemoveUnsafe($data){
  $array = array("\input", "\newread", "\openin", "\closein", "\read");

  for($i=0;$i<count($array);$data= str_replace($array[$i],"",$data),$i++);

  return $data;
}

/**
* Check the string (to avoid SQL injection)
*/

function check($string){
  $pattern="#^[a-zA-Z0-9_.]*$#";
  return preg_match($pattern, $string);
}

/**
* Returns true if the file is Latex code
*/
function isTex($file){
  $coupe = preg_split("/\./", $file);
  $ext=$coupe[count($coupe)-1];
  if($ext == "tex"){
    return 1;
  }else{
    return 2;
  }
}

/**
* Converts a typed file to another type (by its extension)
*/

function tex2other($file,$ext){
  $coupe = preg_split("/\./", $file);
  $i=0;
  $file="";
  for($i=0;$i<count($coupe)-1;$i++){
    $file.=(($i>0)?".":"").$coupe[$i];
  }
  if($i<count($coupe))
    $file.=(($i>0)?".":"").$ext;
  return $file;
}

//------------------------------------------------------------------------
// AUTHENTICATION, CREATION, CONSULTATION, UPDATE, DELETION FUNCTIONS
//------------------------------------------------------------------------

/**
* Creates an user into the DB
*/

function createUser($user, $password){
	mysql_query("INSERT INTO `users` (`login`,`password`) VALUES ('".$user."', '".$password."')");
}

/**
* Creates a project on the server
*/ 

function createProject($user,$project){
  if(!check($project))
    return 0;
  if(!file_exists($user."/".$project)){
    mkdir($user."/".$project,0700);
    return 1;
  }else{
    return 2;
  }
}

/**
* List all projects of the specified user
*/

function listProjects($user){
  if(!file_exists($user))
    mkdir($user, 0700);
  $userdir = opendir($user) or die('Erreur');
  $retour = array();
  $index = 0;
  while($entree = @readdir($userdir)){
    if(is_dir($user.'/'.$entree) && $entree != '.' && $entree != '..') {
      array_push($retour,$entree);
      $index++;
    }
  }
  closedir($userdir);
  return $retour;
}

/**
* Update the name of a project
*/

function renameProject($user, $oldName, $newName){
  $path = $user."/".$oldName;
  $newPath = $user."/".$newName;
  if(file_exists($path)){
    return @rename($path, $newPath);
  }else{
    return false;
  }
}

/**
* Delete a project
*/

function deleteProject($user, $project){
  $path = $user."/".$project;
  $dir = @opendir($path);
  if (!$dir) return false;
  while($dir_file = readdir($dir)) {
    if ($dir_file == '.' || $dir_file == '..') continue;
    $file_path = $path."/".$dir_file;
    if (is_dir($file_path)) {
	$res = deleteProject($user, $project."/".$dir_file);
        if (!$res) return false;
    }else {
        $res = @unlink($file_path);
        if (!$res) return false;
    }
  }
  closedir($dir);
  $res = @rmdir($path);
  if (!$res) return false;
  return true;
}

/**
* List all files in a specified project and for a specified user
*/

function listFiles($user,$project){
  $dossier = $user."/".$project;
  if(!file_exists($user."/".$project))
    mkdir($user,0700);
  $userdir = opendir($user."/".$project) or die('Erreur');
  $retour = array();
  $index = 0;
  while($entree = @readdir($userdir)){
    if(!is_dir($dossier.'/'.$entree) && $entree != '.htaccess' && $entree != '.' && $entree != '..') {
      $cut = preg_split("/\./", $entree);
      $ext=$cut[count($cut)-1];
      if($ext == "png" || $ext == "gif" || $ext == "jpg" || $ext == "jpeg" || $ext == "tex"){
        array_push($retour,$entree);
      }
    }
  }
  closedir($userdir);
  return $retour;
}

/**
* Creates a file in a specified user and for a specifed 
*/

function createFile($user,$project,$file){
  return createFileData($user,$project,$file,"");
}

/**
* Creates a file in a specified user and for a specifed and initiliazes with $data
*/

function createFileData($user,$project,$file,$data){
  if(!check($project) || !check($file))
    return 0;
  if(!file_exists($user."/".$project."/".$file)){
    $fil = fopen($user."/".$project."/".$file, 'w');
    if(isTex($file))
      $data = latexRemoveUnsafe($data);
    fwrite($fil, $data);
    fclose($fil);
    return 1;
  }else{
    return 2;
  }
}

/**
* Update the content of a file
*/

function updateFile($user,$project,$file,$data){
  if(!check($project) || !check($file) || !isTex($file))
    return 0;
  if(file_exists($user."/".$project."/".$file)){
    $fil = fopen($user."/".$project."/".$file, 'w');
    if(isTex($file))
      $data = latexRemoveUnsafe($data);
    fwrite($fil, $data);
    fclose($fil);
    return 1;
  }else{
    return 2;
  }
}

/**
* Rename a file
*/

function renameFile($user, $project, $oldname, $newname){
  $path = $user."/".$project."/".$oldname;
  $newPath = $user."/".$project."/".$newname;
  if(file_exists($path)){
    return @rename($path, $newPath);
  }else{
    return false;
  }
}

/**
* Delete a file
*/

function deleteFile($user, $project, $file){
  $path = $user."/".$project."/".$file;
  if(file_exists($path)){
    return @unlink($path);
  }else{
    return false;
  }
}

//------------------------------------------------------------------------
// REQUEST PROCESSING
//------------------------------------------------------------------------

if(isset($_GET['project'])){
  $_GET['project'] = str_replace("..","",$_GET['project']);
  $_GET['project'] = str_replace("/","",$_GET['project']);
}

if(isset($_GET['file'])){
  $_GET['file'] = str_replace("..","",$_GET['file']);
  $_GET['file'] = str_replace("/","",$_GET['file']);
}

/*Login and password loading (they must be in all requests)*/
if(isset($_GET['login']) && isset($_GET['password'])){
  $login = htmlentities(mysql_real_escape_string($_GET['login']));
  $password = htmlentities(mysql_real_escape_string($_GET['password']));
}else{
    echo returnErrorCode(4);
    exit(4);
}

/*Case 1 : Account creation*/
if(isset($_GET['type']) && $_GET['type'] == "user" && isset($_GET['action']) && $_GET['action'] == "create"){
    if(check($login) == 1){
    /*If the user isn't already registered : continue the registration*/
      $res = mysql_query("SELECT count(*) as res FROM users WHERE login='".$login."'") or die(mysql_error());
      $fetch = mysql_fetch_array($res);
      $resultat = intval($fetch["res"]);
      if($resultat < 1){
        $password = encodePassword($password);
        createUser($login, $password);
        echo buildJSONResponse(array("create" => "ok", "login" => $login, "password" => $password));
      }else{
	echo returnErrorCode(2);
      }
    }else{
	echo returnErrorCode(1);
    }
}

/*Case 2 : Authentication*/
else if(isset($_GET['type']) && $_GET['type'] == "user" && isset($_GET['action']) && $_GET['action'] == 'auth'){
  if(check($login) == 1){
   $password=encodePassword($password);
   if(authenticate($login, $password)){
      echo buildJSONResponse(array("create" => "ok", "login" => $login, "password" => $password));
    }else{
    echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 3 : Request for projects list*/
else if(isset($_GET['type']) && $_GET['type'] == "user" && isset($_GET['action']) && $_GET['action'] == "explore"){
  if(check($login) == 1){
    if(authenticate($login, $password)){
      echo buildJSONResponse(array("create" => "ok", "projets" => listProjects($login)));
    }else{
      echo returnErrorCode(2);
    }
  }else{
     echo returnErrorCode(1);
  }
}

/*Case 4 : Create a project (for a specified user)*/
else if(isset($_GET['type']) && $_GET['type'] == "project" && isset($_GET['action']) && $_GET['action'] == "create" && isset($_GET['project'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  if(check($login) == 1){
     if(authenticate($login, $password)){
       if(createProject($login,$project) == 1){
         echo buildJSONResponse(array("create" => "ok", "projets" => listProjects($login)));
       }else{
         echo returnErrorCode(3); 
       }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}


/*Case 5 : Request for the content of a specified project*/
else if(isset($_GET['type']) && $_GET['type'] == "project" && isset($_GET['action']) && $_GET['action'] == "explore" && isset($_GET['project'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  if(check($login) == 1){
    if(authenticate($login, $password)){
      if(createProject($login,$project) == 2){
        echo buildJSONResponse(array("create" => "ok", "files" => listFiles($login,$project)));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 6 : Request for renaming the specified project*/
else if(isset($_GET['type']) && $_GET['type'] == "project" && isset($_GET['action']) && $_GET['action'] == "update" && isset($_GET['project']) && isset($_GET['newname'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $new_project = htmlentities(mysql_real_escape_string($_GET['newname']));
  if(check($login) == 1){
    if(authenticate($login, $password)){
      if(renameProject($login, $project, $new_project)){
        echo buildJSONResponse(array("create" => "ok", "updated"=>"ok", "projets" => listProjects($login)));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 7 : Delete a specified project*/
else if(isset($_GET['type']) && $_GET['type'] == "project" && isset($_GET['action']) && $_GET['action'] == "delete" && isset($_GET['project'])){
 $project = htmlentities(mysql_real_escape_string($_GET['project']));
 if(check($login) == 1){
    if(authenticate($login, $password)){
      if(deleteProject($login, $project)){
        echo buildJSONResponse(array("create" => "ok", "deleted"=>"ok", "projets" => listProjects($login)));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 8 : Create a new file (for a specified user and in a specified project)*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "create" && isset($_GET['project']) && isset($_GET['file'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $file = htmlentities(mysql_real_escape_string($_GET['file']));
  if(check($login) == 1){
     if(authenticate($login, $password) && isset($_POST['data']) && createFileData($login,$project,$file,base64_decode(htmlentities($_POST['data']))) == 1){
      echo buildJSONResponse(array("create" => "ok", "files" => listFiles($login,$project)));
    }else if(isTex($file)==1 && createFile($login,$project,$file) == 1){
      echo buildJSONResponse(array("create" => "ok", "files" => listFiles($login,$project)));
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}


/*Case 9 : Request for the content of a file (for a specified user and in a specified project)*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "explore" && isset($_GET['project']) && isset($_GET['file'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $file = htmlentities(mysql_real_escape_string($_GET['file']));
  if(check($login) == 1){
    if(authenticate($login, $password)){
      if(createFile($login,$project,$file) == 2){
        $file = file_get_contents($login."/".$project."/".$file);
        echo base64_encode($file);
      }else{
         echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 10 : Update the name of a file*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "update" 
&& isset($_GET['project']) && isset($_GET['file']) && isset($_GET['newname'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $file = htmlentities(mysql_real_escape_string($_GET['file']));
  $new_file = htmlentities(mysql_real_escape_string($_GET['newname']));
  if(check($login) == 1){
    if(authenticate($login, $password)){
      if(renameFile($login, $project, $file, $new_file)){
        echo buildJSONResponse(array("create" => "ok", "updated"=>"ok", "files" => listFiles($login,$project)));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 11 : Update a file*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "update" 
&& isset($_GET['project']) && isset($_GET['file']) && isset($_POST['data'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $file = htmlentities(mysql_real_escape_string($_GET['file']));
  if(check($login) == 1){
    //Modification from keke approved by keke : htmlentities cause weirdness :p
    if(authenticate($login, $password)){
      if(updateFile($login,$project,$file,$_POST['data']) == 1){
        echo buildJSONResponse(array("create" => "ok", "updated" => "ok"));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 12 : Delete a specified file*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "delete" 
&& isset($_GET['project']) && isset($_GET['file'])){
 $project = htmlentities(mysql_real_escape_string($_GET['project']));
 $file = htmlentities(mysql_real_escape_string($_GET['file']));
 if(check($login) == 1){
    if(authenticate($login, $password)){
      if(deleteFile($login, $project, $file)){
        echo buildJSONResponse(array("create" => "ok", "deleted"=>"ok", "files" => listFiles($login,$project)));
      }else{
        echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 13 : Request for Latex Compilation*/
else if(isset($_GET['type']) && $_GET['type'] == "file" && isset($_GET['action']) && $_GET['action'] == "compile" && isset($_GET['project']) && isset($_GET['file'])){
  $project = htmlentities(mysql_real_escape_string($_GET['project']));
  $file = htmlentities(mysql_real_escape_string($_GET['file']));
  if(check($login) == 1){
    if(authenticate($login, $password)){
      if(file_exists($login."/".$project."/".$file)){
        $res = compile($login,$project,$file);
        if(file_exists($login."/".$project."/".tex2other($file,"pdf"))){
          $res = base64_encode(file_get_contents($login."/".$project."/".tex2other($file,"pdf")));
          echo buildJSONResponse(array("create" => "ok", "pdf" => $res));
        }else{
          echo buildJSONResponse(array("create" => "erreur", "dump" => $res));
        }
        @unlink($login."/".$project."/".tex2other($file,"pdf"));
        @unlink($login."/".$project."/".tex2other($file,"log"));
        @unlink($login."/".$project."/".tex2other($file,"aux"));
      }else{
         echo returnErrorCode(3);
      }
    }else{
      echo returnErrorCode(2);
    }
  }else{
    echo returnErrorCode(1);
  }
}

/*Case 14 : Request not supported*/
else{
  echo buildJSONResponse(array("erreur" => "nothing"));
}
?>
