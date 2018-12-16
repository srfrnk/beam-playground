FORCE:

run: FORCE
	@gradle --console=plain clean run > output/build.log 2>&1
