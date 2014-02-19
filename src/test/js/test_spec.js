describe('Start Page', function() {
  ptor = protractor.getInstance();

  beforeEach(function() {
    ptor.get('#/');
    button = ptor.findElement(protractor.By.className('brand').);
    button.click();
  });

  it('says hello', function() {
    message = ptor.findElement(protractor.By.className('message'));
    expect(message.getText()).toEqual('Hello!');
  });
});
