set outdir=output
@RD /S /Q %outdir%
mkdir %outdir%
java.exe -jar C:\Users\a_day_000\IdeaProjects\kotlin2\build\build\libs\httpdl.jar -n 1 -l 1M -f file.txt -o %outdir% -t trace.txt
echo "app terminated"
pause