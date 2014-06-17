rm dist.zip /tmp/dist.zip
zip -r /tmp/dist.zip * -x \*.svn\* cc.php basiclti-sample-cartridge-01.zip materials\* index.php header.php footer.php dist.sh
cp /tmp/dist.zip .
