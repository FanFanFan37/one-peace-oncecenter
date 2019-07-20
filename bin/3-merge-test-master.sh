#!/usr/bin/env bash
cd ../
git checkout test
git pull
git checkout master
git pull
git merge test
git push

