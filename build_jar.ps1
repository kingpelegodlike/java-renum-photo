javac -sourcepath src -d build\classes .\src\RenumPhoto.java -cp .\lib\sanselan-0.97-incubator.jar
jar --create --file .\jar\RenumPhoto.jar --manifest RenumPhoto -C .\build\classes\ .
java -jar .\jar\RenumPhoto.jar