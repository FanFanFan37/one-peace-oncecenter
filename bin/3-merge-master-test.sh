#!/usr/bin/env bash
cd ../
git checkout master
git pull
git checkout test
git pull
git merge master
git push
