set outdir="output2"
@RD /S /Q %outdir%
mkdir %outdir%
java.exe -jar C:\Users\a_day_000\IdeaProjects\kotlin2\build\build\libs\httpdl.jar -n 3 -l 1M -f file_two_large.txt -o %outdir% -t trace.txt
echo "app terminated"
pause