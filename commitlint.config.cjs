module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    // Allow longer headers (default is 100, we set 200)
    'header-max-length': [2, 'always', 200],
  },
};
