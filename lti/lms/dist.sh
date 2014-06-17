rm lms.zip /tmp/lms.zip
zip -r /tmp/lms.zip * -x \*.svn\* config.php dist.sh
cp /tmp/lms.zip .
