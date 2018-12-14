FORCE:

run: FORCE
	kubectl exec -it mgmt-0 -- bash -c "mkdir -p /ride-hailing-dev-ops/beam-test"
	kubectl cp ./build.gradle mgmt-0:/ride-hailing-dev-ops/beam-test/build.gradle
	kubectl cp ./src mgmt-0:/ride-hailing-dev-ops/beam-test/
	kubectl exec -it mgmt-0 -- bash -c "cd /ride-hailing-dev-ops/beam-test && gradle run"

local-run:
	clear && gradle run
