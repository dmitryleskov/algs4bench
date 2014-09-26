set name=%~n0%
java -jar target/microbenchmarks.jar ^
  -jvmArgs "-server -Xcomp" ^
  -r 1 -w 1 -f 1 -wi 5 -i 10 -tu ms ^
  -rf csv -rff %name%.csv -o %name%.log ^
  ".*ArrayCopyVsManualAssignment.*" 
rem -p problemSize=100000000 
rem -gc true ^
