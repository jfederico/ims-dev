<?php

class FOORM {

    public $valid = false;
    public $complete = false;

    // Parse a form field description
    // field:type:key=value:key2=value2
    public static function parseFormString($str) { 
        $op = array(); 
        $pairs = explode(":", $str); 
        foreach ($pairs as $pair) { 
            $kv = explode("=", $pair);
        if ( sizeof($kv) == 1 ) {
                $op[] = $pair;
            } else {
                $op[$kv[0]] = $kv[1];
            }
        } 
        return $op; 
    } 

    // http://technology-ameyaaloni.blogspot.com/2010/06/mysql-to-hsql-migration-tips.html
    /**
     * 
     */
    public static function formSql($fieldinfo, $vendor) {
        $info = FOORM::parseFormString($fieldinfo);
        $field = $info[0];
        $type = $info[1];
        if ( !( isset($field) && isset($type) ) ) {
            throw new Exception(
                    'All model elements must include field name and type');
        }
        if ( $type == 'header' ) return false;
        $maxlength = intval($info['maxlength']);
        if ($maxlength < 1) $maxlength = 80;
        $required = $info['required'];

        $schema = '';
        if ("key" == $type ) {
            if ("hsqldb" == $vendor) {
                $schema = "INTEGER IDENTITY PRIMARY KEY";
            } else if ("oracle" == $vendor) {
                $schema = "INTEGER";
            } else if ("sqlite" == $vendor) {
                $schema = "INTEGER PRIMARY KEY";
            } else {
                $schema = "INTEGER NOT NULL AUTO_INCREMENT";
            }
        } else if ("autodate" == $type ) {
            if ("oracle" == $vendor) {
                $schema = "TIMESTAMP NOT NULL";
            } else {
                $schema = "DATETIME NOT NULL";
            }
        } else if ("integer" == $type ) {
            if ("oracle" == $vendor || $vendor == 'sqlite' ) {
                $schema = "INTEGER";
            } else {
                $schema = "INT";
            }
        } else if ("lkey" == $type || "url" == $type  || "text" == $type  || "textarea" == $type ) {
            if ("oracle" == $vendor) {
                if ($maxlength < 4000) {
                    $schema = "VARCHAR2(" . $maxlength . ")";
                } else {
                    $schema = "CLOB";
                }
            } else if ("sqlite" == $vendor) {
                $schema = "TEXT";
            } else if ("hsqldb" == $vendor) {
                $schema = "VARCHAR(" . $maxlength . ")";
            } else {
                if ($maxlength < 512) {
                    $schema = "VARCHAR(" . $maxlength . ")";
                } else {
                    $schema = "TEXT(" . $maxlength . ")";
                }
            }
        } else if ("radio" == $type  || "checkbox" == $type  ) {
            if ("oracle" == $vendor) {
                $schema = "NUMBER(1) DEFAULT '0'";
            } else {
                $schema = "TINYINT DEFAULT '0'";
            }
        }
        if ($schema == '' ) return false;

        if ($required == 'true' && strpos($schema, 'NOT NULL') === false )
            $schema .= " NOT NULL";
        return "    " . $field . " " . $schema;
    }

    public static function formSqlFields($formDefinition, $vendor) {
        $sb = '';
        foreach ($formDefinition as $formField) {
            $retval = FOORM::formSql($formField, $vendor);
            if ($retval === false) continue;
            if (strlen($sb) > 0) $sb .= ",\n";
            $sb .= $retval;
        }
        return $sb;
    }

    public static function formSqlTable($table, $formDefinition, $vendor, $doReset=false)  {
		$retval = Array();
		if ( $doReset ) $retval[] = 'DROP TABLE ' . $table;
        $theKey = FOORM::formSqlKey($formDefinition);
        $lkeys = FOORM::formSqlLKeys($formDefinition, $vendor);
        $uniques = FOORM::formSqlUniques($formDefinition, $vendor);

		// Not suitable for Oracle yet
		$create = "CREATE TABLE ".$table." (\n" . 
			FOORM::formSqlFields($formDefinition, $vendor);

		if ( $theKey !== false && $vendor != 'sqlite' ) {
			$create .= ",\n    PRIMARY KEY( " . $theKey . " )";
		}
		if ( $lkeys !== false && $vendor != 'sqlite' ) {
			$create .= ",\n" . $lkeys;
		}
		if ( $uniques !== false && $vendor != 'sqlite' ) {
			$create .= ",\n" . $uniques;
		}
		$create .= "\n)\n";
		$retval[] = $create;
		return $retval;
	}

    public static function formSqlKey($formDefinition) {
        $fields = FOORM::fieldsOfType($formDefinition,'key');
        if ( count($fields) > 1 ) {
            throw new Exception('Cannot have more than one key');
        }
        if ( count($fields) == 1 ) return $fields[0];
		return false;
    }

    public static function formSqlLKey($formDefinition) {
        $fields = FOORM::fieldsOfType($formDefinition,'lkey');
        if ( count($fields) > 1 ) {
            throw new Exception('Cannot have more than one logical key');
        }
        if ( count($fields) == 1 ) return $fields[0];
		return false;
    }

    public static function fieldsOfType($formDefinition, $kind) {
        $retval = Array();
        foreach ($formDefinition as $formField) {
            $info = FOORM::parseFormString($formField);
            $field = $info[0];
            $type = $info[1];
            if ( $type != $kind ) continue;
            $retval[] = $field;
        }
        return $retval;
    }

    public static function formSqlLKeys($formDefinition, $vendor) {
        $retval = '';
        $txtlen = 0;
        $count = 0;
        foreach ($formDefinition as $formField) {
            $info = FOORM::parseFormString($formField);
            $field = $info[0];
            $type = $info[1];
			if ( $type != 'lkey' ) continue;
			if ( $type == 'integer' ) {
                $intcount++;
            } else {
				$count++;
			    $maxlength = intval($info['maxlength']);
			    if ($maxlength < 1) $maxlength = 80;
				$txtlen += $maxlength;
			}
        }

		// Reserve 8 bytes for integers - If we don't have enough, split equally
		$avail = 128 - ($intcount * 8);
        $per = -1;
		if ( $txtlen > $avail ) $per = $avail/$count;

        foreach ($formDefinition as $formField) {
            $info = FOORM::parseFormString($formField);
            $field = $info[0];
            $type = $info[1];
			if ( $type != 'lkey' ) continue;
			$maxlength = intval($info['maxlength']);
			if ($maxlength < 1) $maxlength = 80;
			if ( $type != 'integer' ) {
                $len = $maxlength;
				if ( $len > $avail ) $len = $avail;
				if ( $per > 0 && $len > $per ) $len = $per;
				$field .= '('.$len.')';
			}
			if ( strlen($retval) > 0 ) $retval .= ', ';
			$retval .= $field;
        }
		if ( strlen($retval) == 0 ) return false;
        return '    UNIQUE ( '.$retval.' ) ';
    }

    public static function formSqlUniques($formDefinition, $vendor) {
        $retval = '';
        $txtlen = 0;
        $count = 0;
        foreach ($formDefinition as $formField) {
            $info = FOORM::parseFormString($formField);
			if ( $info['unique'] != 'true' ) continue;
            $field = $info[0];
            $type = $info[1];
			if ( $type == 'integer' ) {
                $intcount++;
            } else {
				$count++;
			    $maxlength = intval($info['maxlength']);
			    if ($maxlength < 1) $maxlength = 80;
				$txtlen += $maxlength;
			}
        }

		// Reserve 8 bytes for integers - If we don't have enough, split equally
		$avail = 128 - ($intcount * 8);
        $per = -1;
		if ( $txtlen > $avail ) $per = $avail/$count;

        foreach ($formDefinition as $formField) {
            $info = FOORM::parseFormString($formField);
			if ( $info['unique'] != 'true' ) continue;
            $field = $info[0];
            $type = $info[1];
			$maxlength = intval($info['maxlength']);
			if ($maxlength < 1) $maxlength = 80;
			if ( $type != 'integer' ) {
                $len = $maxlength;
				if ( $len > $avail ) $len = $avail;
				if ( $per > 0 && $len > $per ) $len = $per;
				$field .= '('.$len.')';
			}
			if ( strlen($retval) > 0 ) $retval .= ', ';
			$retval .= $field;
        }
		if ( strlen($retval) == 0 ) return false;
        return '    UNIQUE ( '.$retval.' ) ';
    }

    public static function fieldsWithString($formDefinition, $string) {
        $retval = Array();
        foreach ($formDefinition as $formField) {
			if ( strpos($formField, $string) !== false ) continue;
            $info = FOORM::parseFormString($formField);
            $field = $info[0];
            $retval[] = $field;
        }
        return $retval;
    }
}

if ( ! function_exists('isCli') ) {
    function isCli() {
        $sapi_type = php_sapi_name();
        if (substr($sapi_type, 0, 3) == 'cli' && empty($_SERVER['REMOTE_ADDR'])) {
            return true;
        } else {
            return false;
        }
    }
}

// If we are running from the command line - do a unit test
if ( isCli() ) {
    echo("\nparseFormStrong\n");
    print_r(FOORM::parseFormString('title:text:required=true:maxlength=25'));
    print_r(FOORM::parseFormString('description:textarea:required=true:rows=2:cols=25'));
    print_r(FOORM::parseFormString('sendemail:radio:requred=true:label=bl_sendemail:choices=on,off,part'));

    echo("\nformSql\n");
    echo(FOORM::formSql('title:text:required=true:maxlength=25','mysql'));echo("\n");
    echo(FOORM::formSql('id:key','sqlite'));echo("\n");

    $formFields = Array(
        'id:key',
        'email:lkey:maxlength=128',
        'user_id:integer:unique=true',
        'track_id:integer:unique=true',
        'title:text:required=true:maxlength=25',
        'description:textarea:required=true:rows=2:cols=25',
        'sendemail:radio:requred=true:label=bl_sendemail:choices=on,off,part'
    );

    echo("\nformSqlFields\n");
    echo(FOORM::formSqlFields($formFields,'sqlite'));echo("\n");

    echo("\nformSqlKey\n");
    echo(FOORM::formSqlKey($formFields));echo("\n");

    echo("\nformSqlLKey\n");
    echo(FOORM::formSqlLKey($formFields));echo("\n");

    echo("\nformSqlTable\n");
    print_r(FOORM::formSqlTable('TEST_Table', $formFields, 'sqlite'));echo("\n");
}
