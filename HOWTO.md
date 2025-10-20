# Environment Setup

```shell
git submodule init
git submodule update --init --recursive
uv sync
```

## Install QNN Library

```shell
wget https://softwarecenter.qualcomm.com/api/download/software/sdks/Qualcomm_AI_Runtime_Community/All/2.37.0.250724/v2.37.0.250724.zip -o tmp.zip

unzip tmp.zip ./third_party/ 
```

# Build the project

```shell
export EXECUTORCH_ROOT=./third_party/executorch
export QNN_SDK_ROOT=./third_party/qnn-2.37.0.250724
export ANDROID_NDK_ROOT=
pushd $EXECUTORCH_ROOT
./backends/qualcomm/scripts/build.sh
```
