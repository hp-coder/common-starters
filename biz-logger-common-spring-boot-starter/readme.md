# Biz logger

Biz logger focuses on solving the problem of who did what at what time.

## Abstract

This project has absorbed advantages from both [log-record](https://github.com/qqxx6661/log-record) and
[mzt-biz-log](https://github.com/mouzt/mzt-biz-log/), refined their designs at the same time.

## BizCode 10099 

## V1.0

The framework is almost completely customizable.

### Feature

1. Record biz logs on methods of concrete classes or implementations.
2. Custom functions: Support registering non-static functions using expressions starting with the '#' that SpEL 
   doesn't support natively.

## V2.0

### Planned Feature

1. Diff logs.
2. Ability to process methods annotated on interfaces.
