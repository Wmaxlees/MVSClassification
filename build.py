import glob
import os
import shutil

from subprocess import call

currentDir = os.getcwd()

# Build the MVSIMPL project
os.chdir(currentDir + '/mvsimpl')
call(['mvn', 'install', '-DskipTests'])

for folder in glob.glob(currentDir + '/approaches/*'):
    if folder is currentDir + '/approaches/approach-archetype':
        continue

    os.chdir(folder)
    call(['mvn', 'package', '-DskipTests'])

    for file in glob.glob(folder + '/target/uber*.jar'):
        shutil.copy(file, currentDir + '/mvsui/bin')
    for file in glob.glob(folder + '*.properties'):
        shutil.copy(file, currentDir + '/mvsui/config')

# Build the MVSUI project
os.chdir(currentDir + '/mvsui')
call(['mvn', 'package', '-DskipTests'])
