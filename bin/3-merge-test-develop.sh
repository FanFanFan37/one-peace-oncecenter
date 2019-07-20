#!/usr/bin/env bash
cd ../
git checkout test
git pull
git checkout develop
git pull
git merge test
git push

