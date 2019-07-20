#!/usr/bin/env bash
cd ../
git tag -a v$1 -m 'version '$1
git push origin v$1