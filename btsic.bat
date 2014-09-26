set name=%~n0%
java -jar target/microbenchmarks.jar ^
  -jvmArgs "-server -Xcomp" ^
  -r 1 -w 1 -f 1 -wi 3 -i 5 -tu ms ^
  -rf csv -rff %name%.csv -o %name%.log ^
  -p cutoff=0 ^
  ".*BinaryToSimpleInsertionCutoff\.testBinaryInsertion" 
java -jar target/microbenchmarks.jar ^
  -jvmArgs "-server -Xcomp" ^
  -r 1 -w 1 -f 1 -wi 3 -i 5 -tu ms ^
  -rf csv -rff %name%X.csv -o %name%X.log ^
  ".*BinaryToSimpleInsertionCutoff\.testBinaryInsertionX" 
rem -p problemSize=100000000 
rem -gc true ^
