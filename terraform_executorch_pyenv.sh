#/bin/bash

set -e 

uv python pin 3.12
uv init
uv add executorch torch ipykernel


git submodule add https://github.com/pytorch/executorch ./third_party/executorch

commit=$(python3 -c "import executorch.version as v; print(v.git_version)")
git submodule update --init --recursive third_party/executorch
git -C third_party/executorch fetch origin "${commit}" && git -C third_party/executorch checkout "${commit}"

uv add ./third_party/executorch/extension/llm/tokenizers 