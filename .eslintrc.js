// https://eslint.org/docs/user-guide/configuring

module.exports = {
  root: true,
  parser: 'babel-eslint',
  parserOptions: {
    sourceType: 'module'
  },
  env: {
    browser: true,
  },
  // https://github.com/standard/standard/blob/master/docs/RULES-en.md
  extends: 'standard',
  // required to lint *.vue files
  plugins: [
    'html'
  ],
  'no-tabs': 0,    
  'no-mixed-spaces-and-tabs': 0,   
  'indent': ["off", "tab"],    
  'no-trailing-spaces': 0, 

  // add your custom rules here
  rules: {
    'indent': ["off", "tab"],   
    // allow async-await
    "space-before-function-paren": 0,
    'generator-star-spacing': 'off',
    // allow paren-less arrow functions    
    'arrow-parens': 0,          
    'no-tabs': 0,   
    'no-mixed-spaces-and-tabs': 0,    
    'no-unused-vars': 0,    
    'no-trailing-spaces': 0,    
    "eqeqeq": 0  ,
	// allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
  }
}
