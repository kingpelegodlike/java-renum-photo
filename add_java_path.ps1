$windows_path = $env:Path
$windows_path = ($windows_path.Split(';') | Where-Object { $_ -ne 'C:\Program Files (x86)\Common Files\Oracle\Java\javapath' }) -join ';'
$addPath = 'C:\Program Files\Java\jdk-11.0.4\bin\'
$env:Path = $windows_path + ";" + $addPath
$env:Path