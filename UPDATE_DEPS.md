# Update dependabot dependencies

Since there are lots of projects in here  we get lots of dependabot PRs.  This is a quick way to update all of them.

```bash
# create local updates branch
git branch updates
git switch updates

# merge all dependabot branches into updates branch

for branch in  $(gitbranch -a --sort=-committerdate | grep dependa); do echo "merging ${branch}" &&  git merge --no-edit $branch updates ; done

# in case of a conflict resolve it and continue with the merge
git merge --continue

# push the updates branch

git push origin updates

# create a PR for the updates branch

# merge the PR

# delete the dependabot branches
for branch in  $(git branch -a --sort=-committerdate | grep dependa); do  git branch --remote -d ${branch#remotes/} && git push origin -d ${branch#remotes/origin/}; done

# delete the updates branch
git branch -d updates

```