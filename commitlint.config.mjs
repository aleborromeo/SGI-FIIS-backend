export default {
  extends: ['@commitlint/config-conventional'],
  ignores: [
    (message) => message === 'Refactor: Translate reports module (reportes) to English (reports)',
  ],
  rules: {
    'header-max-length': [2, 'always', 200],
  },
};
