describe('Start Page', function() {
  ptor = protractor.getInstance();
  ptor.get("#/");

  it('shows toolbar', function() {
    message = ptor.findElement(protractor.By.className('brand'));
    expect(message.getText()).toEqual('textManager');
  });
});
