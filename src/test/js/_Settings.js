describe('Settings Page', function() {
  ptor = protractor.getInstance();

  beforeEach(function() {
    ptor.get("#/Settings");
  })

  it('shows table', function() {
    ptor.findElements(protractor.By.className('ngHeaderText')).then(function(elems) {
      expect(elems.length).toEqual(2);
    });
  });

  it('shows rows', function() {
    ptor.findElements(protractor.By.repeater('row in renderedRows')).then(function(elems) {
      expect(elems.length > 0).toBe(true);
    });
  })
});
