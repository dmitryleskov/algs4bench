rem Run testManualCopy() with chunkSize of 1 
rem to illustrate the slowdown after warmup
set name=%~n0%
java -jar target/microbenchmarks.jar ^
  -jvmArgs "-server -Xcomp" ^
  -r 1 -w 1 -f 1 -wi 5 -i 10 -tu ms ^
  -rf csv -rff %name%.csv -o %name%.log ^
  -p chunkSize=1 ^
  ".*ArrayCopyVsManualAssignment.testManualCopy" 
