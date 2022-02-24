[issues]: https://github.com/wmontwe/gradle-plugins/issues

[pull-request]: https://github.com/wmontwe/gradle-plugins/pulls

# Contributing

When contributing to this project, this document should help you get started.

## Issues

We use GitHub Issues to track bugs and enhancements, please feel free to open [issues][issues] for:

* _**Questions**_ help us to improve the user experience
* _**Ideas**_ are a great source for contributions
* _**Problems**_ show where this project is lacking

If you're reporting a problem, please help us by providing as much information as possible.
Ideally, that includes a description or small example how to reproduce the problem.

## Contribute code

### Develop

Create a branch according to the following rules, work on your changes and create a pull-request.

#### Branch

Every change has to branch of from `main` and use this branch naming convention:

* `feature/{type_of_change}-{short_description}` or with ticket
  id `feature/{ticket_id}/{type_of_change}-{short_description}`

##### Type of change

* *added* for new features or functionality
* *changed* for changes in existing features or functionality
* *deprecated* for soon-to-be removed features
* *removed* | for removed features or functionality
* *fixed* for any bug fixes
* *security* in case of vulnerabilities
* *bumped* for dependency updates

Examples:

- `feature/ISSUE-456/added-awesome-hashing-algorithm`
- `feature/added-awesome-hashing-algorithm`
- `feature/removed-not-so-awesome-algorithm`
- `feature/fixed-algorithm-corner-case`
- `feature/bumped-lib-to-1.3.0`

### Pull request

[Pull requests][pull-request] are a great way to improve the project. But please, discuss your
contribution with us before making changes.

If you contribute, you have:

* made clear which problem you're trying to solve
* followed following rules

#### Create pull request

Please use our title pattern: `[{issue id}] {type of change} {short description}`:

* Optional: Add `issue id` in brackets if you have any, otherwise leave it out.
* `type of change` e.g Added, Changed, ...
* `short description` of your change

Example:

* Added awesome hashing algorithm
* [Issue-156] Changed thumbnail generation

Pull requests must fill the provided template. Put N/A when a paragraph cannot be filled.

*Labels* should be used (enhancement,bugfix, help wanted etc...) to categorise your contribution.

#### Code review

We will review your contribution and check following criteria:

* [ ] Functional and fitting in the project
* [ ] Code style and naming conventions followed
* [ ] Test written and passing
* [ ] Continuous Integration build passing
* [ ] Cross platform testing done for all supported platforms
* [ ] Documentation updated
* [ ] Changelog updated

`main` must be always in releasable state.

### Components using other licenses

Contributing code and introducing dependencies into the repository from other projects that use one
of the following licenses is allowed.

* [MIT](https://opensource.org/licenses/MIT)
* [ISC](https://opensource.org/licenses/ISC)
* [Apache 2.0](https://opensource.org/licenses/Apache-2.0)

Any other contribution needs to be signed off by the project owners.
