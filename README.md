# linkmatrx
Web Crawler tool for search engine optimization and website management

## Requirements:
Java 8+ [tested]

Maven 3.6+ [tested]

## Run it
1.) Download Zip file and un-Zip

2.) Run:> mvn package

3.) cd target

4.) Run:> java -jar linkmatrx-1.0-jar-with-dependencies.jar -m url -u http://www.website.com -o console  -d 1


### Flags
-m = mode: (url: scan by URL)

-u = url to scan

-o = output: (console: output to console)(csv: output to CSV file)

-d = depth of scan

### Output to a text file

> java -jar linkmatrx-1.0-jar-with-dependencies.jar -m url -u http://www.website.com -o console  -d 1 > results.txt

