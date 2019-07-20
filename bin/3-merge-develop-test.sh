#!/usr/bin/env bash
cd ../
git checkout develop
git pull
git checkout test
git pull
git merge develop
git push
