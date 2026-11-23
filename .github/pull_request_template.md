## Summary
<!-- Concise explanation of what changed and why -->

## Linear Issue
- Closes `LUC-`

## Screenshots / Screen Recording (UI Changes)
| Dark Mode | Light Mode |
| :---: | :---: |
| *(attach screenshot)* | *(attach screenshot)* |

## Engineering Checklist
- [ ] Code builds cleanly and passes `./kotlin test -m shared -p jvm`
- [ ] Android (`./kotlin build -m androidApp`) and Web Wasm (`./kotlin build -m webApp`) compile without new warnings
- [ ] Design System tokens used (no hardcoded colors, magic `dp` sizes, or ad-hoc styles)
- [ ] Both **Dark Mode** and **Light Mode** verified
- [ ] Offline persistence / Room migrations (if applicable) tested and `progress.json` backwards-compatible
- [ ] Gitleaks pre-commit scan passed (`no leaks found`)

## Security & Privacy Check
- [ ] No secrets, tokens, or personal credentials committed
- [ ] External URLs, intents, or imported JSON inputs validated safely (if touched)
